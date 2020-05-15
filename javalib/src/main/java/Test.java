import com.xjava.down.XDownload;
import com.xjava.down.base.IDownloadRequest;
import com.xjava.down.base.IRequest;
import com.xjava.down.data.Response;
import com.xjava.down.listener.OnConnectListener;
import com.xjava.down.listener.OnDownloadConnectListener;
import com.xjava.down.listener.OnDownloadListener;
import com.xjava.down.listener.OnProgressListener;
import com.xjava.down.listener.OnResponseListener;

public class Test{
    public static void main(String[] args){
//        XDownload.download("http://down.dgtle.com/app/dgtle3.9/dgtle_v3.9.2.apk")
//                 .setUseMultiThread(false)
//                 .setConnectListener(new OnDownloadConnectListener(){
//                     @Override
//                     public void onPending(IDownloadRequest request){
//                         System.out.println("onPending");
//                     }
//
//                     @Override
//                     public void onStart(IDownloadRequest request){
//                         System.out.println("onStart");
//                     }
//
//                     @Override
//                     public void onConnecting(IDownloadRequest request){
//                         System.out.println("onConnecting="+request.getTotalLength());
//                     }
//
//                     @Override
//                     public void onRequestError(IDownloadRequest request,int code,String error){
//                         System.out.println("onRequestError");
//                     }
//
//                     @Override
//                     public void onCancel(IDownloadRequest request){
//                         System.out.println("onCancel");
//                     }
//
//                     @Override
//                     public void onRetry(IDownloadRequest request){
//                         System.out.println("onRetry");
//                     }
//                 })
//                 .setOnProgressListener(new OnProgressListener(){
//                     @Override
//                     public void onProgress(IDownloadRequest request,float progress){
//                         System.out.println("onProgress="+(int)(progress*100));
//                     }
//                 })
//                 .setDownloadListener(new OnDownloadListener(){
//                     @Override
//                     public void onComplete(IDownloadRequest request){
//                         System.out.println("onComplete");
//                     }
//
//                     @Override
//                     public void onFailure(IDownloadRequest request){
//                         System.out.println("onFailure");
//                     }
//                 })
//                 .start();
//        XDownload.request("http://www.baidu.com/link?url=ByBJLpHsj5nXx6DESXbmMjIrU5W4Eh0yg5wCQpe3kCQMlJK_RJBmdEYGm0DDTCoTDGaz7rH80gxjvtvoqJuYxK")
        XDownload.request("https://www.zhihu.com/question/20583607/answer/16597802")
//        XDownload.request("http://hexapixel.com/download.php?file=com.hexapixel.widgets.ribbon.alphatest.src.jar")
                 .setOnResponseListener(new OnResponseListener(){
                     @Override
                     public void onResponse(IRequest request,Response response){
                         System.out.println("onResponse="+response.code()+" result="+response.result());
                     }

                     @Override
                     public void onError(IRequest request,Exception exception){
                         System.out.println("onError");
                     }
                 }).setOnConnectListener(new OnConnectListener(){
            @Override
            public void onPending(IRequest request){
                System.out.println("onPending");
            }

            @Override
            public void onStart(IRequest request){
                System.out.println("onStart");
            }

            @Override
            public void onConnecting(IRequest request){
                System.out.println("onConnecting");
            }

            @Override
            public void onCancel(IRequest request){
                System.out.println("onCancel");
            }

            @Override
            public void onRetry(IRequest request){
                System.out.println("onRetry");
            }
        }).start();
    }

}
