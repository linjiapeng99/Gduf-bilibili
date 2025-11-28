package com.gduf.bilibili.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.domain.Danmu;
import com.gduf.bilibili.domain.constant.UserContant;
import com.gduf.bilibili.domain.constant.UserMomentsConstant;
import com.gduf.bilibili.service.DanmuService;
import com.gduf.bilibili.service.config.WebSocketConfig;
import com.gduf.bilibili.service.util.RocketMQUtil;
import com.gduf.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {
    //日志记录
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    //在线人数，即连接服务端的客户端的人数
    private static final AtomicInteger ONLINE_COUNT=new AtomicInteger(0);
    //创建一个map集合，用来保存已连接成功的客户端
    public static final ConcurrentHashMap<String,WebSocketService>WEBSOCKET_MAP=new ConcurrentHashMap<>();
    //每当与客户端成功连接成功，那么就创建一个会话对象
    private Session session;
    //会话对象的唯一id
    private String sessionId;
    //会话的用户id
    private Long userId;

    private static ApplicationContext APPLICATION_CONTEXT;

    public static void setApplicationContext(ApplicationContext context){
        WebSocketService.APPLICATION_CONTEXT=context;
    }
    @OnOpen
    public void openConnection(Session session,@PathParam("token") String token){
        try{
            Long userId = TokenUtil.verifyToken(token);
        }catch (Exception ignored){}
        this.sessionId=session.getId();
        this.session=session;
        if(WEBSOCKET_MAP.containsKey(sessionId)){//当前客户端是老的客户端
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId,this);
        }else {//当前客户端为新的客户端
            WEBSOCKET_MAP.put(sessionId,this);
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户连接成功："+sessionId+",当前在线人数为："+ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        }catch (Exception e){
            logger.error("连接异常");
        }
    }


    @OnClose
    public void closeConnection(){
        if(WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出："+sessionId+",当前在线人数为："+ONLINE_COUNT);
    }

    @OnMessage
    public void onMessage(String message){
        logger.info("用户信息："+sessionId+",报文："+message);
        if(!StringUtils.isNullOrEmpty(message)){
            try {
                //群发消息
                for(Map.Entry<String, WebSocketService> entry:WEBSOCKET_MAP.entrySet()){
                    WebSocketService webSocketService = entry.getValue();
                    DefaultMQProducer producer =(DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");
                    JSONObject jsonObject=new JSONObject();
                    //将接收消息的sessionId和消息一起发出去，mq的消费者才知道将消息发给哪个客户端
                    jsonObject.put("sessionId",webSocketService.getSessionId());
                    jsonObject.put("message",message);
                    Message msg=new Message(UserMomentsConstant.TOPIC_DANMUS,jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                    //异步发送
                    RocketMQUtil.ayncSendMessage(producer,msg);

                    /*if(webSocketService.session.isOpen()){
                        webSocketService.sendMessage(message);
                    }*/
                }
                if(this.userId!=null){
                    //保存弹幕到数据库中
                    Danmu danmu= JSONObject.parseObject(message,Danmu.class);
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService =(DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                    danmuService.addDanmu(danmu);
                    danmuService.addDanmusToRedis(danmu);
                }
            }catch (Exception e){
                logger.error("弹幕接收异常");
                e.printStackTrace();
            }
        }
    }
    //向前端推送当前观看在线人数  5秒
    @Scheduled(fixedRate =5000)
    public void noticeOnlineCount() throws IOException {
        for (Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if(webSocketService.getSession().isOpen()){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("onlineCount",ONLINE_COUNT.get());
                jsonObject.put("msg","当前在线人数为："+ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }
    @OnError
    public void onError(Throwable error){

    }
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
