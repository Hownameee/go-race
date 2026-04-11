package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClubListPayload {
    @SerializedName("clubs")
    private List<NetworkClub> clubs;

    @SerializedName("type")
    private String type;

    public List<NetworkClub> getClubs() { return clubs; }
    public String getType() { return type; }
}
