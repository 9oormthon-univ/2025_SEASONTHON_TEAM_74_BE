package com.example.demo.room.dto.req;

import com.example.demo.room.entity.enums.Mode;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

public class RoomReq {

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateRoom {
        @NotNull(message = "닉네임은 필수입니다.")
        private String nickname;

        @Min(value = 1, message = "최소 인원은 2명 이상이어야 합니다.")
        @Max(value = 36, message = "최대 인원은 10명 이하여야 합니다.")
        private Integer maxMember;

        private String pwd;
        private String inviteCode;

        @Min(value = 2, message = "최소 팀은 2팀 이상이어야 합니다.")
        @Max(value = 6, message = "최대 팀은 6팀 이하여야 합니다.")
        private Integer maxTeam;

        @Min(value = 1, message = "최소 라운드는 1 이상이어야 합니다.")
        @Max(value = 15, message = "최대 라운드는 15 이하여야 합니다.")
        private Integer maxRound;
        private Mode mode;
        private String yearSet;

        @NotNull(message = "시드머니는 필수입니다.")
        @Min(value = 100000, message = "최소 시드머니는 100,000 이상이어야 합니다.")
        @Max(value = 1000000, message = "최대 시드머니는 1,000,000 이하여야 합니다.")
        private Integer seedMoney;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JoinRoom {
        @NotNull(message = "닉네임은 필수입니다.")
        private String nickname;
        private String pwd;
        private String inviteCode;
    }
}
