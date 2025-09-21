package com.medusa.gruul.overview.api.exception;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 类描述
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Data
@Accessors(chain = true)
public class RemoteResult<T> implements Serializable {

  private boolean success;
  private T data;
  private String message;
  private Exception exception;

  /**
   * 失败
   * @param message 失败提示语
   * @return 远程调用结果
   * @param <T> 返回类型
   */
  public static <T> RemoteResult<T> error(String message,Exception e){
    RemoteResult remoteResult=new RemoteResult();
    remoteResult.setSuccess(Boolean.FALSE).setException(e).setMessage(message);
    return  remoteResult;
  }

  /**
   * 失败
   * @param message 失败提示语
   * @return 远程调用结果
   * @param <T> 返回类型
   */
  public static <T> RemoteResult<T> error(String message){
    return error(message,null);
  }

  /**
   * 成功
   * @return
   * @param <T> 返回类型
   */
  public static <T> RemoteResult success(){
    return success(null);
  }

  /**
   * 成功
   * @param data 返回数据
   * @return 结果
   * @param <T> 类型
   */
  public static <T> RemoteResult success(T data){
    RemoteResult remoteResult=new RemoteResult();
    remoteResult.setSuccess(Boolean.TRUE).setData(data);
    return  remoteResult;
  }

  public boolean isSuccess(){
    return success;
  }
}
