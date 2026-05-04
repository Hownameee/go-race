package com.grouprace.core.network.model.search;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NetworkClubsResponse {
    @SerializedName("clubs")
    private List<NetworkUserSearch> clubs;

    public List<NetworkUserSearch> getClubs() { return clubs; }
    public void setClubs(List<NetworkUserSearch> clubs) { this.clubs = clubs; }
}
