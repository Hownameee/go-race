package com.grouprace.core.network.model.club;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubPayload {
    @SerializedName("clubs")
    private List<NetworkClub> clubs;

    public List<NetworkClub> getClubs() { return clubs; }
}
