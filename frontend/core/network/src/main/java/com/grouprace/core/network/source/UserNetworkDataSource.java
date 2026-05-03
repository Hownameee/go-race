package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.UserApiService;
import com.grouprace.core.network.model.user.AvatarUploadResponse;
import com.grouprace.core.network.model.user.ChangePasswordPayload;
import com.grouprace.core.network.model.user.ConfirmEmailChangePayload;
import com.grouprace.core.network.model.user.FollowListResponse;
import com.grouprace.core.network.model.user.MyProfileInfoPayload;
import com.grouprace.core.network.model.user.ProfileOverviewResponse;
import com.grouprace.core.network.model.user.RequestNewEmailOtpPayload;
import com.grouprace.core.network.model.user.ResetPasswordWithOtpPayload;
import com.grouprace.core.network.model.user.VerifyEmailOtpPayload;
import com.grouprace.core.network.model.user.VerifyCurrentPasswordPayload;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNetworkDataSource {
    private static final int FOLLOW_LIST_LIMIT = 100;
    private final UserApiService apiService;

    @Inject
    public UserNetworkDataSource(UserApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<ProfileOverviewResponse>> getMyOverview() {
        MutableLiveData<Result<ProfileOverviewResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getMyOverview().enqueue(new Callback<ApiResponse<ProfileOverviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileOverviewResponse>> call,
                                   Response<ApiResponse<ProfileOverviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProfileOverviewResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Load profile overview failed.";
                        Log.e("UserDataSource", "Overview API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("UserDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileOverviewResponse>> call, Throwable t) {
                Log.e("UserDataSource", "Overview Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<ProfileOverviewResponse>> getUserOverview(int userId) {
        MutableLiveData<Result<ProfileOverviewResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserOverview(userId).enqueue(new Callback<ApiResponse<ProfileOverviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileOverviewResponse>> call,
                                   Response<ApiResponse<ProfileOverviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProfileOverviewResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Load user profile overview failed.";
                        Log.e("UserDataSource", "User Overview API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("UserDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileOverviewResponse>> call, Throwable t) {
                Log.e("UserDataSource", "User Overview Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<FollowListResponse>> getFollowers(int userId) {
        MutableLiveData<Result<FollowListResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getFollowers(userId, FOLLOW_LIST_LIMIT).enqueue(new Callback<ApiResponse<FollowListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowListResponse>> call,
                                   Response<ApiResponse<FollowListResponse>> response) {
                handleFollowListResponse(liveData, response, "Load followers failed.");
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowListResponse>> call, Throwable t) {
                Log.e("UserDataSource", "Followers Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<FollowListResponse>> getFollowing(int userId) {
        MutableLiveData<Result<FollowListResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getFollowing(userId, FOLLOW_LIST_LIMIT).enqueue(new Callback<ApiResponse<FollowListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FollowListResponse>> call,
                                   Response<ApiResponse<FollowListResponse>> response) {
                handleFollowListResponse(liveData, response, "Load following failed.");
            }

            @Override
            public void onFailure(Call<ApiResponse<FollowListResponse>> call, Throwable t) {
                Log.e("UserDataSource", "Following Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<MyProfileInfoPayload>> getMyInfo() {
        MutableLiveData<Result<MyProfileInfoPayload>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getMyInfo().enqueue(new Callback<ApiResponse<MyProfileInfoPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyProfileInfoPayload>> call,
                                   Response<ApiResponse<MyProfileInfoPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<MyProfileInfoPayload> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Load my profile info failed.";
                        Log.e("UserDataSource", "MyInfo API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("UserDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyProfileInfoPayload>> call, Throwable t) {
                Log.e("UserDataSource", "MyInfo Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Void>> requestEmailChangeOtp() {
        return executeVoidCall(apiService.requestEmailChangeOtp(),
                "Request email OTP failed.");
    }

    public LiveData<Result<Void>> verifyEmailChangeOtp(String otpCode) {
        return executeVoidCall(apiService.verifyEmailChangeOtp(new VerifyEmailOtpPayload(otpCode)),
                "Verify email OTP failed.");
    }

    public LiveData<Result<Void>> requestNewEmailChangeOtp(String newEmail) {
        return executeVoidCall(apiService.requestNewEmailChangeOtp(new RequestNewEmailOtpPayload(newEmail)),
                "Request new email OTP failed.");
    }

    public LiveData<Result<Void>> confirmEmailChange(String newEmail, String otpCode) {
        return executeVoidCall(apiService.confirmEmailChange(new ConfirmEmailChangePayload(newEmail, otpCode)),
                "Confirm email change failed.");
    }

    public LiveData<Result<Void>> changePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        return executeVoidCall(apiService.changePassword(
                        new ChangePasswordPayload(oldPassword, newPassword, confirmNewPassword)),
                "Change password failed.");
    }

    public LiveData<Result<Void>> verifyCurrentPassword(String oldPassword) {
        return executeVoidCall(
                apiService.verifyCurrentPassword(new VerifyCurrentPasswordPayload(oldPassword)),
                "Verify current password failed."
        );
    }

    public LiveData<Result<Void>> requestPasswordResetOtp() {
        return executeVoidCall(apiService.requestPasswordResetOtp(),
                "Request password reset OTP failed.");
    }

    public LiveData<Result<Void>> resetPasswordWithOtp(String otpCode, String newPassword, String confirmNewPassword) {
        return executeVoidCall(apiService.resetPasswordWithOtp(
                        new ResetPasswordWithOtpPayload(otpCode, newPassword, confirmNewPassword)),
                "Reset password failed.");
    }

    public LiveData<Result<Void>> deleteMyAccount() {
        return executeVoidCall(apiService.deleteMyAccount(), "Delete account failed.");
    }

    public LiveData<Result<String>> uploadMyAvatar(byte[] avatarBytes, String fileName, String mimeType) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        String safeMimeType = mimeType != null && !mimeType.trim().isEmpty()
                ? mimeType
                : "image/jpeg";
        String safeFileName = fileName != null && !fileName.trim().isEmpty()
                ? fileName
                : "avatar.jpg";

        RequestBody requestBody = RequestBody.create(
                avatarBytes,
                MediaType.parse(safeMimeType)
        );
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData(
                "avatar",
                safeFileName,
                requestBody
        );

        apiService.uploadMyAvatar(avatarPart).enqueue(new Callback<ApiResponse<AvatarUploadResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AvatarUploadResponse>> call,
                                   Response<ApiResponse<AvatarUploadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AvatarUploadResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()
                            && apiResponse.getData() != null
                            && apiResponse.getData().getAvatarUrl() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getAvatarUrl()));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Upload avatar failed.";
                        Log.e("UserDataSource", "Upload Avatar API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("UserDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AvatarUploadResponse>> call, Throwable t) {
                Log.e("UserDataSource", "Upload Avatar Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<Void>> updateMyInfo(MyProfileInfoPayload payload) {
        return executeVoidCall(apiService.updateMyInfo(payload), "Update profile failed.");
    }

    public void updateMyInfoSync(MyProfileInfoPayload payload) throws Exception {
        Response<ApiResponse<Void>> response = apiService.updateMyInfo(payload).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP Error: " + response.code() + " " + response.message());
        }

        ApiResponse<Void> apiResponse = response.body();
        if (!apiResponse.isSuccess()) {
            throw new Exception(apiResponse.getMessage() != null
                    ? apiResponse.getMessage()
                    : "Update profile failed.");
        }
    }

    public ProfileOverviewResponse getMyOverviewSync() throws Exception {
        Response<ApiResponse<ProfileOverviewResponse>> response = apiService.getMyOverview().execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP Error: " + response.code() + " " + response.message());
        }

        ApiResponse<ProfileOverviewResponse> apiResponse = response.body();
        if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
            throw new Exception(apiResponse.getMessage() != null
                    ? apiResponse.getMessage()
                    : "Load profile overview failed.");
        }

        return apiResponse.getData();
    }

    public MyProfileInfoPayload getMyInfoSync() throws Exception {
        Response<ApiResponse<MyProfileInfoPayload>> response = apiService.getMyInfo().execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP Error: " + response.code() + " " + response.message());
        }

        ApiResponse<MyProfileInfoPayload> apiResponse = response.body();
        if (!apiResponse.isSuccess() || apiResponse.getData() == null) {
            throw new Exception(apiResponse.getMessage() != null
                    ? apiResponse.getMessage()
                    : "Load my profile info failed.");
        }

        return apiResponse.getData();
    }

    private LiveData<Result<Void>> executeVoidCall(Call<ApiResponse<Void>> call, String fallbackMessage) {
        MutableLiveData<Result<Void>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>(null));
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : fallbackMessage;
                        Log.e("UserDataSource", "Update API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("UserDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("UserDataSource", "Update Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    private void handleFollowListResponse(
            MutableLiveData<Result<FollowListResponse>> liveData,
            Response<ApiResponse<FollowListResponse>> response,
            String fallbackMessage
    ) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<FollowListResponse> apiResponse = response.body();

            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                liveData.postValue(new Result.Success<>(apiResponse.getData()));
            } else {
                String msg = apiResponse.getMessage() != null
                        ? apiResponse.getMessage()
                        : fallbackMessage;
                Log.e("UserDataSource", "Follow List API Failed: " + msg);
                liveData.postValue(new Result.Error<>(new Exception(msg), msg));
            }
        } else {
            String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
            Log.e("UserDataSource", errorMessage);
            liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
        }
    }
}
