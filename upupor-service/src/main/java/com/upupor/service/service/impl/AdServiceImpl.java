package com.upupor.service.service.impl;

import com.upupor.service.dao.entity.Ad;
import com.upupor.service.dao.mapper.AdMapper;
import com.upupor.service.dto.page.AdIndexDto;
import com.upupor.service.dto.page.ad.*;
import com.upupor.service.service.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告服务
 *
 * @author YangRunkang(cruise)
 * @date 2020/01/28 09:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdMapper adMapper;

    @Value("${codingvcr.ads}")
    private String adIds;

    @Override
    public AdIndexDto getAdByUrl(String requestUrl) {
        AdIndexDto adIndexDto = new AdIndexDto();
        try {
            if (StringUtils.isEmpty(adIds)) {
                return adIndexDto;
            }
            List<Ad> all = adMapper.all();
            if (CollectionUtils.isEmpty(all)) {
                return adIndexDto;
            }

            // all-top,all-bottom,tech-right-class,tech-right-promote,tech-right-ad,qa,share,workplace

            // ******************全局Start******************
            AllBottomAdDto allBottomAdDto = new AllBottomAdDto();
            allBottomAdDto.setAd(getByAdId(all, "all-bottom"));

            AllTopAdDto allTopAdDto = new AllTopAdDto();
            allTopAdDto.setAd(getByAdId(all, "all-top"));

            adIndexDto.setAllBottomAdDto(allBottomAdDto);
            adIndexDto.setAllTopAdDto(allTopAdDto);
            // ******************全局End******************

            if (requestUrl.contains("qa")) {
                QaAdDto qaAdDto = new QaAdDto();
                qaAdDto.setAd(getByAdId(all, "qa"));

                adIndexDto.setQaAdDto(qaAdDto);
            }

            if (requestUrl.contains("share")) {
                ShareAdDto shareAdDto = new ShareAdDto();
                shareAdDto.setAd(getByAdId(all, "share"));

                adIndexDto.setShareAdDto(shareAdDto);
            }

            if (requestUrl.contains("tech")) {
                TechRightClassAdDto techRightClassAdDto = new TechRightClassAdDto();
                techRightClassAdDto.setAd(getByAdId(all, "tech-right-class"));

                TechRightAdDto techRightAdDto = new TechRightAdDto();
                techRightAdDto.setAd(getByAdId(all, "tech-right-ad"));

                TechRightPromoteAdDto techRightPromoteAdDto = new TechRightPromoteAdDto();
                techRightPromoteAdDto.setAd(getByAdId(all, "tech-right-promote"));

                adIndexDto.setTechRightClassAdDto(techRightClassAdDto);
                adIndexDto.setTechRightPromoteAdDto(techRightPromoteAdDto);
                adIndexDto.setTechRightAdDto(techRightAdDto);
            }


            if (requestUrl.contains("workplace")) {
                WorkplaceAdDto workplaceAdDto = new WorkplaceAdDto();
                workplaceAdDto.setAd(getByAdId(all, "workplace"));

                adIndexDto.setWorkplaceAdDto(workplaceAdDto);
            }

            return adIndexDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adIndexDto;
    }

    /**
     * all-top,all-bottom,tech-right-class,tech-right-promote,tech-right-ad,qa,share,workplace
     *
     * @param all
     * @param adId
     * @return
     */
    private Ad getByAdId(List<Ad> all, String adId) {
        Ad ad = new Ad();

        if (CollectionUtils.isEmpty(all)) {
            return ad;
        }

        List<Ad> collect = all.stream()
                .filter(o -> o.getAdId().equals(adId))
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            log.warn("无匹配");
            return ad;
        }

        ad = collect.get(0);

        return ad;
    }


}
