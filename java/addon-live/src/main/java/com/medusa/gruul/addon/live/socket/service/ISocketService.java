package com.medusa.gruul.addon.live.socket.service;

import java.util.List;

/**
 * @author miskw
 * @date 2023/6/21
 * @describe 描述
 */
public interface ISocketService {
    /**
     * 关闭这个直播间socket通道
     * @param liveIds 直播间ids
     */
    void closeSocketChannel(List<String> liveIds);

}
