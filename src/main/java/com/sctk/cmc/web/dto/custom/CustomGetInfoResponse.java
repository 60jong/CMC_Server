package com.sctk.cmc.web.dto.custom;

import com.sctk.cmc.domain.Custom;
import com.sctk.cmc.domain.CustomStatus;
import com.sctk.cmc.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomGetInfoResponse {
    private String title;
    private Long memberId;
    private String memberName;
    private String memberProfileImgUrl;
    private Integer desiredPrice;
    private CustomStatus accepted;

    public static CustomGetInfoResponse of(Custom custom) {
        Member member = custom.getMember();

        return CustomGetInfoResponse.builder()
                .title(custom.getTitle())
                .memberId(member.getId())
                .memberName(member.getName())
                .memberProfileImgUrl(member.getProfileImgUrl())
                .desiredPrice(custom.getDesiredPrice())
                .accepted(custom.getAccepted())
                .build();
    }
}
