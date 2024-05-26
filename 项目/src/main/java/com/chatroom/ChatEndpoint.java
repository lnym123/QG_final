package com.chatroom;

import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ServerEndpoint(value = "/chat")
public class ChatEndpoint {
    Lock lock = new ReentrantLock();
    private Session session;
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static final Map<String, String> customIdToSessionMap = new ConcurrentHashMap<>(); // 新增：存储自定义ID与SessionMAP的映射
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Session>> groupSessions = new ConcurrentHashMap<>();
    public static final  Map<String,String> IdToGroupName=new HashMap<String,String>();//存用户和群组之间的对应关系
    private static final Map<String,HashMap> EachGroups=new HashMap<>();//储存相应的群名字以及群成员，hashmap为群成员
    private static final Map<String,Session>IdToSession=new HashMap<>();
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        chatEndpoints.add(this);
    }
    //处理新用户加入
    private void handleNewUser(JSONObject jsonMsg) {
        String groupName = jsonMsg.getString("theGroup");
        IdToGroupName.put(jsonMsg.getString("theId"),groupName);
        //为每一个session指定一个相应的id
        IdToSession.put(jsonMsg.getString("theId"),this.session);

        if (!groupSessions.containsKey(groupName)) {
            groupSessions.put(groupName, new CopyOnWriteArraySet<>());
        }
        groupSessions.get(groupName).add(session); // 为对应群组的chatEndpoints添加属于这个群组的session（用户）
        String userId=jsonMsg.getString("theId");
        if (!customIdToSessionMap.containsKey(userId)) {  //添加对应的用户名给当前连接的session
            customIdToSessionMap.put(this.session.getId(),userId);
        }
        if (!EachGroups.containsKey(groupName)) {
            EachGroups.put(groupName, new HashMap());
        }
        for (Map.Entry<String, String> entry : IdToGroupName.entrySet()) {
            String Id = entry.getKey(); // 获取用户ID
            String TheGName = entry.getValue(); // 获取群组名
            if(TheGName.equals(groupName)) {
                EachGroups.get(groupName).put(Id,"");
            }
        }
        //发送在线名单
        BroadCastAllUsers(groupName);

    }
    //发送在线名单给用户
    private void BroadCastAllUsers(String groupName){
        //获得与当前用户同群组的成员，并且广播

        String mapAsString = EachGroups.get(groupName).toString();
        //构建广播所有群成员的消息
        Map<String,String> message=new HashMap<>();
        message.put("action","TheonlineUsersList");
        message.put("theGroup",groupName);
        message.put("content",mapAsString);
        JSONObject UsersMessage = new JSONObject(message);
        broadcast(UsersMessage);

    }

    @OnClose
    public void onClose(Session session) {
        //构建离开群组的消息
        Map<String,String> message=new HashMap<>();
        String groupName=IdToGroupName.get(customIdToSessionMap.get(this.session.getId()));
        message.put("action","chat");
        message.put("theId",customIdToSessionMap.get(this.session.getId()));
        message.put("theGroup",IdToGroupName.get(customIdToSessionMap.get(this.session.getId())));
        message.put("content","离开了当前聊天室");
        JSONObject jsonMsg = new JSONObject(message);
        lock.lock();
        //删除该用户在对应装有该群内所有成员的名单

        // 对于 EachGroups 的修改
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, HashMap> entry : EachGroups.entrySet()) {
            String GId = entry.getKey();
            if (GId.equals(IdToGroupName.get(customIdToSessionMap.get(this.session.getId())))) {
                keysToRemove.add(customIdToSessionMap.get(this.session.getId()));
            }
        }
        for (String key : keysToRemove) {
            for (Map<String, HashMap> eachGroup : EachGroups.values()) {
                eachGroup.remove(key);
            }
        }

        // 对于 IdToGroupName 的修改
        keysToRemove.clear();
        for (Map.Entry<String, String> entry : IdToGroupName.entrySet()) {
            String Id = entry.getKey();
            if (Id.equals(customIdToSessionMap.get(this.session.getId()))) {
                keysToRemove.add(Id);
            }
        }
        for (String key : keysToRemove) {
            IdToGroupName.remove(key);
        }
        chatEndpoints.remove(this);
        lock.unlock();

        broadcast(jsonMsg);
        BroadCastAllUsers(groupName);

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject jsonMsg = new JSONObject(message);  //处理新连接判断是否存在相应的群组名
            if ("newUser".equals(jsonMsg.getString("action"))) {
                handleNewUser(jsonMsg);
                broadcast(jsonMsg);
            } else if("OpenPrivate".equals(jsonMsg.getString("action"))){
                //另对方进入私聊
                HandlePrivateChat(jsonMsg,"open");
            }else if("private".equals(jsonMsg.getString("action"))){
               //发送私聊
                HandlePrivateChat(jsonMsg,"chat");
            }else if("LeavePrivate".equals(jsonMsg.getString("action"))){
                //私聊用户退出
                HandlePrivateChat(jsonMsg,"leave");
            }else{
                // 其他消息类型处理
                broadcast(jsonMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //处理私聊
    public void HandlePrivateChat(JSONObject jsonMsg ,String type){
        String ObjectId = jsonMsg.getString("TheObject").trim();//清空前面的空格
        Session session=IdToSession.get(ObjectId);
        if(type.equals("open")){
            //为对应用户发送该请求
            try {//处理私聊请求,另对方进入私聊页面
                JSONObject privateMsg = new JSONObject();
                privateMsg.put("event", "OpenPrivateChat");
                privateMsg.put("object", jsonMsg.getString("theId"));
                session.getBasicRemote().sendText(privateMsg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("chat")){
            try { //发送私聊请求以及其消息
                JSONObject privateMsg = new JSONObject();
                privateMsg.put("event", "PrivateChat");
                privateMsg.put("object", jsonMsg.getString("theId"));//发送人
                privateMsg.put("content", jsonMsg.getString("content"));
                session.getBasicRemote().sendText(privateMsg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {//私聊用户退出，另当前用户也退出
                JSONObject privateMsg = new JSONObject();
                privateMsg.put("event", "LEAVEChat");
                session.getBasicRemote().sendText(privateMsg.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }
//消息发送
    private void broadcast(JSONObject jsonMsg) {
        String groupName = jsonMsg.getString("theGroup");
        CopyOnWriteArraySet<Session> sessions = groupSessions.get(groupName);
        //发送的是新用户进入聊天室
        if("newUser".equals(jsonMsg.getString("action"))){
            if (sessions != null) {
                sessions.forEach(s -> {
                    if (s.isOpen()&& !s.equals(session)) {
                        try {
                            // 创建一个JSONObject来存储消息
                            JSONObject jsonMessage = new JSONObject();
                            jsonMessage.put("event", "userJoined");
                            jsonMessage.put("userId", jsonMsg.getString("theId"));
                            jsonMessage.put("content", "加入了群组");

                            // 将JSON对象转换为字符串
                            String jsonString = jsonMessage.toString();

                            // 使用sendText方法发送JSON格式的消息
                            s.getBasicRemote().sendText(jsonString);

                        } catch (IOException e) {
                            e.printStackTrace();
                            // 处理发送失败的情况，可能需要移除失效的session
                        }
                    }
                });
            }
            //发送离开群组的消息
        }else if("离开了当前聊天室".equals(jsonMsg.getString("content"))){
            if (sessions != null) {
                sessions.forEach(s -> {
                    if (s.isOpen()&& !s.equals(session)) {
                        try {
                            // 创建一个JSONObject来存储消息
                            JSONObject jsonMessage = new JSONObject();
                            jsonMessage.put("event","LogOut");
                            jsonMessage.put("userId", jsonMsg.getString("theId"));
                            jsonMessage.put("content", jsonMsg.getString("content"));
                            // 将JSON对象转换为字符串
                            String jsonString = jsonMessage.toString();
                            // 使用sendText方法发送JSON格式的消息
                            s.getBasicRemote().sendText(jsonString);

                        } catch (IOException e) {
                            e.printStackTrace();
                            // 处理发送失败的情况，可能需要移除失效的session
                        }
                    }
                });
            }

        }//发送所有在线用户
        else if("TheonlineUsersList".equals(jsonMsg.getString("action"))){
            System.out.println("发送所有在线用户");
            sessions.forEach(s -> {
                if (s.isOpen()) {
                    try {
                        // 创建一个JSONObject来存储消息
                        JSONObject jsonMessage = new JSONObject();
                        jsonMessage.put("event","onlineUsersList");
                        jsonMessage.put("content", jsonMsg.getString("content"));
                        // 将JSON对象转换为字符串
                        String jsonString = jsonMessage.toString();
                        // 使用sendText方法发送JSON格式的消息
                        s.getBasicRemote().sendText(jsonString);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 处理发送失败的情况，可能需要移除失效的session
                    }
                }
            });

        }else  {
            //正常聊天发送至相应群组聊天室
            if (sessions != null) {
                sessions.forEach(s -> {
                    if (s.isOpen()) {
                        try {
                                // 创建一个JSONObject来存储消息
                                JSONObject jsonMessage = new JSONObject();
                                jsonMessage.put("event", "chat");
                                jsonMessage.put("userId", jsonMsg.getString("theId"));
                                jsonMessage.put("content",":"+ jsonMsg.getString("content"));
                                // 将JSON对象转换为字符串
                                String jsonString = jsonMessage.toString();
                                // 使用sendText方法发送JSON格式的消息
                                s.getBasicRemote().sendText(jsonString);

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