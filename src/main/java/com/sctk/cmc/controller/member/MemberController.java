package com.sctk.cmc.controller.member;

import com.sctk.cmc.auth.domain.SecurityMemberDetails;
import com.sctk.cmc.common.exception.ResponseStatus;
import com.sctk.cmc.common.response.BaseResponse;
import com.sctk.cmc.controller.common.dto.ProfileImgPostResponse;
import com.sctk.cmc.controller.designer.dto.LikedDesignerInfoResponse;
import com.sctk.cmc.controller.member.dto.*;
import com.sctk.cmc.controller.member.product.dto.LikeProductGetExistenceResponse;
import com.sctk.cmc.controller.member.product.dto.LikedProductInfoResponse;
import com.sctk.cmc.domain.Designer;
import com.sctk.cmc.domain.Product;
import com.sctk.cmc.service.member.MemberService;
import com.sctk.cmc.service.member.dto.*;
import com.sctk.cmc.service.member.like.handler.function.adapter.LikeFunctionAdapter;
import com.sctk.cmc.service.member.product.MemberProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "구매자 API Document")
public class MemberController {

    private final MemberService memberService;
    private final MemberProductService memberProductService;
    private final LikeFunctionAdapter likeFunctionAdapter;

    // 구매자 간단 정보 조회
    @GetMapping("/{memberId}/info")
    @Operation(summary = "구매자 간단 정보 조회", description = "디자이너가 요청으로 연결된 구매자의 간단 정보를 조회합니다.")
    public BaseResponse<MemberInfoResponse> getMemberInfo(@PathVariable("memberId") Long memberId) {
        MemberInfo memberInfo = memberService.retrieveInfoById(memberId);

        return new BaseResponse<>(new MemberInfoResponse(memberInfo));
    }

    @GetMapping("/detail")
    @Operation(summary = "구매자 상세 정보 조회", description = "구매자가 자신의 상세 정보를 조회합니다.")
    public BaseResponse<MemberDetailResponse> getMemberDetail() {
        MemberDetail memberDetail = memberService.retrieveDetailById(getMemberId());

        return new BaseResponse<>(new MemberDetailResponse(
                memberDetail.getName(),
                memberDetail.getNickname(),
                memberDetail.getEmail(),
                memberDetail.getProfileImgUrl(),
                memberDetail.getIntroduce()
            )
        );
    }

    @GetMapping("/body-info")
    @Operation(summary = "구매자 신체 정보 조회", description = "구매자의 신체 정보를 조회합니다.")
    public BaseResponse<BodyInfoView> getBodyInfo() {
        BodyInfoView infoView = memberService.retrieveBodyInfoById(getMemberId());

        return new BaseResponse<>(infoView);
    }
    @PostMapping("/body-info")
    @Operation(summary = "구매자 신체 정보 등록", description = "구매자의 신체 정보를 등록합니다.")
    public BaseResponse<ResponseStatus> postBodyInfo(@RequestBody BodyInfoPostRequest request) {
        BodyInfoParams bodyInfoParams = new BodyInfoParams(request.getSizes());

        memberService.registerBodyInfo(getMemberId(), bodyInfoParams);

        return new BaseResponse<>(ResponseStatus.SUCCESS);
    }

    @PutMapping("/body-info")
    @Operation(summary = "구매자 신체 정보 수정", description = "구매자 신체 정보를 수정합니다.")
    public BaseResponse<ResponseStatus> putBodyInfo(@RequestBody BodyInfoPutRequest request) {
        BodyInfoModifyParams bodyInfoModifyParams = new BodyInfoModifyParams(request.getSizes());

        memberService.modifyBodyInfo(getMemberId(), bodyInfoModifyParams);

        return new BaseResponse<>(ResponseStatus.SUCCESS);
    }

    @GetMapping("/profiles/status")
    @Operation(summary = "구매자 필수 프로필 작성 여부 조회", description = "구매자의 필수 프로필 작성 여부를 조회합니다.")
    public BaseResponse<ResponseStatus> getMemberProfileStatus() {
        memberService.checkRequirements(getMemberId());

        return new BaseResponse<>(ResponseStatus.SUCCESS);
    }

    @Operation(summary = "디자이너 좋아요 확인 API", description = "해당 디자이너에 좋아요를 누른 기록이 있는지 확인합니다.")
    @GetMapping("likes/designer/{designerId}")
    public BaseResponse<LikeDesignerGetExistenceResponse> checkLikeDesigner(@PathVariable("designerId") Long designerId) {
        LikeDesignerGetExistenceResponse response = memberService.checkLiked(getMemberId(), designerId);

        return new BaseResponse<>(response);
    }

    @GetMapping("/likes/designer")
    @Operation(summary = "찜한 디자이너 조회", description = "좋아요 처리가 된 디자이너를 조회합니다.")
    public BaseResponse<List<LikedDesignerInfoResponse>> getAllLikedDesigner() {

        List<LikedDesignerInfoResponse> responses = memberService.retrieveAllLikedDesignerInfo(getMemberId());
        return new BaseResponse<>(responses);
    }

    @PostMapping("/likes/designer")
    @Operation(summary = "디자니어 좋아요 처리", description = "디자이너에 좋아요 처리를 합니다.")
    public BaseResponse<LikeResponse> postLikeForDesigner(@RequestParam(name = "designer-id") Long designerId) {
        LikeResponse response = likeFunctionAdapter.handle(getMemberId(), designerId, Designer.class);

        return new BaseResponse<>(response);
    }

    @GetMapping("/likes/product")
    @Operation(summary = "찜한 상품 조회", description = "좋아요 처리가 된 상품을 조회합니다.")
    public BaseResponse<List<LikedProductInfoResponse>> getAllLikedProduct() {

        List<LikedProductInfoResponse> responses = memberProductService.retrieveAllInfoById(getMemberId());
        return new BaseResponse<>(responses);
    }

    @Operation(summary = "상품 좋아요 확인 API", description = "해당 상품에 좋아요를 누른 기록이 있는지 확인합니다.")
    @GetMapping("likes/product/{productId}")
    public BaseResponse<LikeProductGetExistenceResponse> checkLikeProduct(@PathVariable("productId") Long productId) {
        LikeProductGetExistenceResponse responses = memberProductService.checkLiked(getMemberId(), productId);

        return new BaseResponse<>(responses);
    }

    @PostMapping("/likes/product")
    @Operation(summary = "상품 좋아요 처리", description = "상품에 좋아요 처리를 합니다.")
    public BaseResponse<LikeResponse> postLikeForProduct(@RequestParam(name = "product-id") Long productId) {
        LikeResponse response = likeFunctionAdapter.handle(getMemberId(), productId, Product.class);

        return new BaseResponse<>(response);
    }

    @PostMapping(value = "/profiles/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "구매자 프로필 사진 등록", description = "구매자 프로필 사진을 등록합니다.")
    public BaseResponse<ProfileImgPostResponse> postProfileImg(@RequestPart("file") MultipartFile profileImg) {
        ProfileImgPostResponse response = memberService.registerProfileImg(getMemberId(), profileImg);

        return new BaseResponse<>(response);
    }

    private Long getMemberId() {
        SecurityMemberDetails memberDetails = (SecurityMemberDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return memberDetails.getId();
    }
}
