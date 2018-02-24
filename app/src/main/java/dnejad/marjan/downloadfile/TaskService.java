package dnejad.marjan.downloadfile;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import io.reactivex.Observable;

/**
 * Created by Marjan.Dnejad
 * on 2/21/2018.
 */

public interface TaskService {

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);
}
