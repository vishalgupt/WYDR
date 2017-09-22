package wydr.sellers.network;

import com.google.gson.JsonElement;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import wydr.sellers.acc.CategoryHolder;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.gson.AddOnlineCart;
import wydr.sellers.gson.CreateOrder;
import wydr.sellers.gson.FavProdHolder;
import wydr.sellers.gson.FavoriteHolder;
import wydr.sellers.gson.HifiDeal;
import wydr.sellers.gson.MakeOrder;
import wydr.sellers.gson.OrderInDeal;
import wydr.sellers.gson.OrderStatus;
import wydr.sellers.gson.OrdersHolder;
import wydr.sellers.gson.ProductHolder;
import wydr.sellers.gson.PrimaryUser;
import wydr.sellers.gson.ProductList;
import wydr.sellers.gson.QueryHolder;
import wydr.sellers.gson.SellerList;
import wydr.sellers.gson.SharedHolder;
import wydr.sellers.gson.UpdateAddress;
import wydr.sellers.gson.UpdateDeal;
import wydr.sellers.gson.coupon;
import wydr.sellers.modal.AddNewConnection;
import wydr.sellers.modal.IssueDetails;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

//import com.squareup.okhttp.logging.HttpLoggingInterceptor;

/**
 * Created by surya on 23/1/16.
 */
public class RestClient
{
    private static GitApiInterface gitApiInterface;
    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

    public static GitApiInterface getClient() {
        if (gitApiInterface == null) {

            OkHttpClient okClient = new OkHttpClient();
            okClient.setReadTimeout(60, TimeUnit.SECONDS);
            okClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            okClient.interceptors().add(httpLoggingInterceptor);

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AppUtil.URL)
                    .addConverter(String.class, new ToStringConverter())
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            gitApiInterface = client.create(GitApiInterface.class);
        }
        return gitApiInterface;
    }

    public interface GitApiInterface
    {
        @GET("3.0/categories?get_images=true&simple=true&item_per_page=0&force_product_count=1&sort_by=category_id&sort_order=desc")
        Call<CategoryHolder> getCategory(@Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @POST("3.0/orders")
        Call<JsonElement> bookOrder(@Body CreateOrder order, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @PUT("3.0/users/{id}")
        Call<JsonElement> UpdateAddress(@Path("id") String groupId,@Body UpdateAddress add, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        // @Headers("Content-Type: application/json")
        @POST("3.0/orders")
        Call<JsonElement> bookMyOrder(@Body CreateOrder order, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @POST("newconnection")
        Call<JsonElement> newConnection(@Body AddNewConnection addNewConnection, @Header("Authorization") String token);

        @POST("3.0/orders")
        Call<String> bookMyOrders(
                @Body MakeOrder order,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);


        @PUT("3.0/orderupdate/{order}")
        Call<JsonElement> updateOrder(@Path("order") String orderId, @Body OrderStatus status, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @POST("dealorder")
        Call<JsonElement> bookOrderInDeal(@Body OrderInDeal order, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @PUT("issues/{id}")
        Call<JsonElement> issueDetails(@Path("id") String groupId,@Body IssueDetails issueDetails, @Header("Authorization") String token);

        @PUT("deal/{user}")
        Call<JsonElement> updateDeal(@Path("user") String user, @Body UpdateDeal order, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @POST("deal")
        Call<JsonElement> sendDeal(@Body HifiDeal order, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @GET("otp")
        Call<JsonElement> sendOtp(@Query("phone") String json, @Header("Authorization") String accept, @Header("Accept") String type);

//.........................//akshay codeing///......................................................

        @PUT("home/569")
        Call<JsonElement> getbanner(@Query("current_user_id") String user, @Body CreateOrder o, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @POST("cart")
        Call<JsonElement>addtocart(@Body AddOnlineCart cart, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @GET("payments")
        Call<JsonElement> getpaymode(@Query("current_user_id") String user, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @POST("otp")
        Call<JsonElement>v_otp(@Body JSONObject jsonObject, @Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @PUT("promotions/{user}")
        Call<JsonElement>promo_cart_download(@Path("user") String user,@Body String ab,@Header("Authorization") String token, @Header("Content-Type") String accept, @Header("Accept") String type);

        @GET("rewardpoints/{userId}")
        Call<JsonElement> getUserRewardHistory(
                @Path("userId") String user_id,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept);
//..................................................................................................

        @GET("queries")
        Call<QueryHolder> getFeed(
                @Query("current_user_id") String userId,
                @Query("page") int page,
                @Query("sort_order") String sortingOrder,
                @Query("sort_by") String sortby,
                @Query("category_id") String categoryId,
                @Query("date_from") String dateFrom,
                @Query("date_to") String dateTo,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("queries")
        Call<QueryHolder> getSearchFeed(
                @Query("search_data") String search_data,
                @Query("page") int page,
                @Query("current_user_id") String userId,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("favouriteitem")
        Call<FavoriteHolder> getFavQuery(
                @Query("user_id") String user_id,
                @Query("object_type") String object_type,
                @Query("page") int page,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("favouriteitem")
        Call<FavProdHolder> getFavProd(
                @Query("user_id") String user_id,
                @Query("object_type") String object_type,
                @Query("page") int page,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("3.0/users")
        Call<JsonElement> getPrimary(
                @Query("company_id") String user_id,
                @Query("get_primary") String object_type,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("users/{userId}")
        Call<JsonElement> getUserDetails(
                @Path("userId") String user_id,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);


        @GET("3.0/categories/{categories}/3.0/products")
        Call<ProductList> getProduct(@Path("categories") String category,
                                     @Query("status") String status,
                                     @Query("current_user_id") String userId,
                                     @Query("only_short_fields") String sort,
                                     @Query("display_type") String displayType,
                                     @Query("sort_by") String sortBy,
                                     @Query("page") int page,
                                     @Query("sort_order") String sortingOrder,
                                     @Query("product_visibility") String visibility,
                                     @Query("filter_hash_array") String filter,
                                     @Query("user_detail") String userDetail,
                                     @Header("Authorization") String token,
                                     @Header("Content-Type") String accept,
                                     @Header("Accept") String type);


        @GET("queries")
        Call<QueryHolder> getSellerQuery(
                @Query("current_user_id") String userId,
                @Query("page") int page,
                @Query("vendor_id") String vendorId,
                @Query("sort_order") String sortOrder,
                @Query("sort_by") String sortBy,
                @Query("category_id") String catId,
                @Query("date_from") String dateFrom,
                @Query("date_to") String dateTo,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("vendors/{companyid}/3.0/products")
        Call<ProductList> getSellersProduct(
                @Path("companyid") String companyid,
                @Query("get_image") String getImage,
                @Query("page") int page,
                @Query("user_detail") String userDetail,
                @Query("status") String status,
                @Query("sort_order") String sortOrder,
                @Query("sort_by") String sortBy,
                @Query("cid") String catId,
                @Query("filter_hash_array") String filter,
                @Query("product_visibility") String visibility,
                @Query("current_user_id") String userId,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("sellers")
        Call<SellerList> getSellers(
                @Query("user_type") String userType,
                @Query("page") int page,
                @Query("get_image") String getImage,
                @Query("sort_order") String sortOrder,
                @Query("sort_by") String sortBy,
                @Query("category_id") String catId,
                @Query("is_root") String isRoot,
                @Query("location[]") ArrayList<String> location,
                @Query("name") String name,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("sellers")
        Call<JsonElement> getMyQuery(
                @Query("user_type") String userType,
                @Query("page") int page,
                @Query("get_image") String getImage,
                @Query("sort_order") String sortOrder,
                @Query("sort_by") String sortBy,
                @Query("category_id") String catId,
                @Query("is_root") String isRoot,
                @Query("location[]") ArrayList<String> location,
                @Query("name") String name,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("vendors/{company_id}/orders")
        Call<OrdersHolder> OrdersReceived(
                @Path("company_id") String companyId,
                @Query("order_request") String request,
                @Query("page") int page,
                @Query("company_name") String companyName,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("orders")
        Call<OrdersHolder> OrdersPlaced(

                @Query("user_id") String userId,
                @Query("page") int page,
                @Query("company_name") String companyName,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("shareditem")
        Call<SharedHolder> SharedByME(

                @Query("detail_view") String detailView,
                @Query("page") int page,
                @Query("user_id") String userId,
                @Query("shared_type") String sharedType,
                @Query("shared_item") String item,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("shareditem")
        Call<SharedHolder> SharedWithMe(

                @Query("detail_view") String detailView,
                @Query("page") int page,
                @Query("user_id") String userId,
                @Query("shared_type") String sharedType,
                @Query("shared_item") String item,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("3.0/vendors/{userId}/categories")
        Call<JsonElement> GetUserCategory(
                @Path("userId") String userId,
                @Query("simple") String simple,
                @Query("force_product_count") String count,
                @Query("company_ids") String companyId,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @PUT("3.0/users/{id}")
        Call<JsonElement> makePrimary(
                @Path("id") String id,
                @Body PrimaryUser primaryUser,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("3.0/categories")
        Call<wydr.sellers.gson.CategoryHolder> FetchCategory(
                @Query("simple") String simple,
                @Query("force_product_count") String productCount,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);

        @GET("3.0/products")
        Call<ProductHolder> getMyProducts(
                @Query("company_id") String companyId,
                @Query("sort_by") String sortBy,
                @Query("sort_order") String order,
                @Query("syncperiod") String period,
                @Query("time_from") String from,
                @Query("page") int page,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept,
                @Header("Accept") String type);


        @GET("business/{userId}")
        Call<JsonElement> getUserKycStatus(
                @Path("userId") String user_id,
                @Header("Authorization") String token,
                @Header("Content-Type") String accept);

    }

}


