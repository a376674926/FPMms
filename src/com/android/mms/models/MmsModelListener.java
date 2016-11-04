package com.android.mms.models;

import java.util.Map;

public interface MmsModelListener {

	
    /**
     * 处理成功回调
     */
    public void onSuccess(Map<String, Object> responseData) ;

    /**
     * 处理错误回调
     */
    public  void onError(int errorCode,String errorMsg) ;
    
}
