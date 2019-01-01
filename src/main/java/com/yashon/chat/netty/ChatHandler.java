package com.yashon.chat.netty;

import com.yashon.chat.enums.MsgActionEnum;
import com.yashon.chat.service.UserService;
import com.yashon.chat.utils.JsonUtils;
import com.yashon.chat.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	// 用于记录和管理所有客户端的channle
	public static ChannelGroup users = 
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) 
			throws Exception {
		// 获取客户端传输过来的消息
		String content = msg.text();
		DataContent dataContent = JsonUtils.jsonToPojo(content,DataContent.class);
		Integer action = dataContent.getAction();

		Channel currentChannel = ctx.channel();

		//2.判断消息类型，根据不同的消息类型处理不同的业务
		if(MsgActionEnum.CONNECT.type == action.intValue()){
			//  2.1 当websocket第一次open的时候，初始化channel，需要将用户的channel和userId进行绑定
			UserChannelRel.put(dataContent.getChatMsg().getSenderId(),currentChannel);
		}else if(MsgActionEnum.CHAT.type == action.intValue()){
			//  2.2 将消息存入数据库，标记消息的状态（未读）
			ChatMsg chatMsg = dataContent.getChatMsg();
			String message = chatMsg.getMsg();
			String receiverId = chatMsg.getReceiverId();
			String senderId = chatMsg.getSenderId();
			//获取spring注入的service
			UserService userService = (UserService)SpringUtil.getBean("userServiceImpl");
			String msgId = userService.saveMessage(chatMsg);
			chatMsg.setMsgId(msgId);

			//从全局用户channel关系中获取接收方的channel
			Channel receiverChannel = UserChannelRel.get(receiverId);
			if(receiverChannel == null){
				//channel为空代表离线，推送消息
			}else{
				//从channelGroup中查找对应的channel是否存在
				Channel channel = users.find(receiverChannel.id());
				if(channel != null){
					//写出消息
					receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(chatMsg)));
				}else{

					//用户离线，推送消息
				}
			}
		}else if(MsgActionEnum.SIGNED.type == action.intValue()){
			//  2.3 消息类型，针对具体的消息进行签收，修改数据库中对应的签收状态（改为已读）
			UserService userService = (UserService)SpringUtil.getBean("userServiceImpl");
			//扩展字段在signed类型中，代表需要去签收的消息id，用逗号隔开
			String msgIdStr = dataContent.getExtand();
			String[] msgids = msgIdStr.split(",");
			List<String> msgIdList = new ArrayList<>();
			for (String msgid : msgids) {
				if(StringUtils.isNotBlank(msgid)){
					msgIdList.add(msgid);
				}
			}

			if(msgIdList !=null && !msgIdList.isEmpty()){

				userService.updateSignedMsg(msgIdList);
			}

		}else if(MsgActionEnum.KEEPALIVE.type == action.intValue()){
			//  2.4 心跳类型
		}
	}
	
	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		
		String channelId = ctx.channel().id().asShortText();
		System.out.println("客户端被移除，channelId为：" + channelId);
		
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		users.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		// 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
		ctx.channel().close();
		users.remove(ctx.channel());
	}
}
