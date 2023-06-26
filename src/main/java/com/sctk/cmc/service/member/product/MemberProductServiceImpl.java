package com.sctk.cmc.service.member.product;

import com.sctk.cmc.common.exception.CMCException;
import com.sctk.cmc.controller.member.product.dto.LikeProductGetExistenceResponse;
import com.sctk.cmc.controller.member.product.dto.LikedProductInfoResponse;
import com.sctk.cmc.domain.DescriptionImg;
import com.sctk.cmc.domain.Member;
import com.sctk.cmc.domain.Product;
import com.sctk.cmc.repository.member.product.MemberProductRepository;
import com.sctk.cmc.service.member.MemberService;
import com.sctk.cmc.service.member.like.product.LikeProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.sctk.cmc.common.exception.ResponseStatus.PRODUCT_ILLEGAL_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberProductServiceImpl implements MemberProductService {
    private final MemberService memberService;
    private final MemberProductRepository memberProductRepository;
    private final LikeProductService likeProductService;

    @Override
    public Product retrieveById(Long productId) {
        return memberProductRepository.findByDesignerIdAndId(productId)
                .orElseThrow(() -> new CMCException(PRODUCT_ILLEGAL_ID));
    }

    @Override
    public LikedProductInfoResponse retrieveInfoById(Long memberId, Long productId) {
        Product product = retrieveById(productId);

        List<String> descriptionImgList = convertToUrlList(product);

        return LikedProductInfoResponse.of(product, product.getDesigner(), descriptionImgList, true);
    }

    @Override
    public List<LikedProductInfoResponse> retrieveAllInfoById(Long memberId) {
        Member member = memberService.retrieveById(memberId);

        return member.getProductLikes()
                .stream()
                .map(likeProduct -> retrieveInfoById(memberId, likeProduct.getProduct().getId()))
                .collect(Collectors.toList());
    }

    private static List<String> convertToUrlList(Product product) {
        return product.getDescriptionImgList().stream()
                .map(DescriptionImg::getUrl)
                .collect(Collectors.toList());
    }

    @Override
    public LikeProductGetExistenceResponse checkLiked(Long memberId, Long productId) {
        Member member = memberService.retrieveById(memberId);

        boolean liked = member.getProductLikes()
                .stream()
                .anyMatch(likeProduct -> likeProduct.getProduct().getId() == productId);
        
        return LikeProductGetExistenceResponse.of(liked);
    }
}
