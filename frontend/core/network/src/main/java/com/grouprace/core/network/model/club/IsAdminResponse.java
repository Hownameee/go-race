package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

public class IsAdminResponse {
    @SerializedName("is_admin")
    private boolean isAdmin;

    public boolean isAdmin() { return isAdmin; }
}
