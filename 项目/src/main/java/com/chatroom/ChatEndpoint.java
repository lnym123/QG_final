package com.chatroom;

import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/chat")
public class ChatEndpoint {

    private Session session;
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static final Map<String, String> customIdToSessionMap = new ConcurrentHashMap<>(); // 新增：存储自定义ID与Session的映射
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Session>> groupSessions = new ConcurrentHashMap<>();
    public static final  Map<String,String> IdToGroupName=new HashMap<String,String>();//存用户和群组之间的对应关系
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        chatEndpoints.add(this);
    }

    private void handleNewUser(JSONObject jsonMsg) {
        String groupName = jsonMsg.getString("theGroup");
        IdToGroupName.put(jsonMsg.getString("theId"),groupName);
        if (!groupSessions.containsKey(groupName)) {
            groupSessions.put(groupName, new CopyOnWriteArraySet<>());
        }
        groupSessions.get(groupName).add(session); // 为对应群组的chatEndpoints添加属于这个群组的session（用户）
        String userId=jsonMsg.getString("theId");
        if (!customIdToSessionMap.containsKey(userId)) {  //添加对应的用户名给当前连接的session
            customIdToSessionMap.put(this.session.getId(),userId);
        }
    }

    @OnClose
    public void onClose(Session session) {
        chatEndpoints.remove(this);
        //构建离开群组的消息
        Map<String,String> message=new HashMap<>();
        message.put("action","chat");
        message.put("theId",customIdToSessionMap.get(this.session.getId()));
        message.put("theGroup",IdToGroupName.get(customIdToSessionMap.get(this.session.getId())));
        message.put("content","离开了当前聊天室");
        JSONObject jsonMsg = new JSONObject(message);
        broadcast(jsonMsg);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        try {
            JSONObject jsonMsg = new JSONObject(message);  //处理新连接判断是否存在相应的群组名
            if ("newUser".equals(jsonMsg.getString("action"))) {
                handleNewUser(jsonMsg);
                broadcast(jsonMsg);
            } else {
                // 其他消息类型处理
                broadcast(jsonMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    private void broadcast(JSONObject jsonMsg) {
        String groupName = jsonMsg.getString("theGroup");
        CopyOnWriteArraySet<Session> sessions = groupSessions.get(groupName);
        //发送的是新用户进入聊天室
        if("newUser".equals(jsonMsg.getString("action"))){
            if (sessions != null) {
                sessions.forEach(s -> {
                    if (s.isOpen()) {
                        try {
                            s.getBasicRemote().sendText(jsonMsg.getString("theId")+"加入了当前聊天室");
                        } catch (IOException e) {
                            e.printStackTrace();
                            // 处理发送失败的情况，可能需要移除失效的session
                        }
                    }
                });
            }
        }else {  //正常聊天发送至相应群组聊天室
            if (sessions != null) {
                sessions.forEach(s -> {
                    if (s.isOpen()) {
                        try {
                            s.getBasicRemote().sendText(jsonMsg.getString("theId")+":"+jsonMsg.getString("content"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            // 处理发送失败的情况，可能需要移除失效的session
                        }
                    }
                });
            }
        }

    }
}