package com.medusa.gruul.addon.team.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.team.model.dto.TeamDTO;
import com.medusa.gruul.addon.team.model.dto.TeamPageDTO;
import com.medusa.gruul.addon.team.model.dto.TeamProductDTO;
import com.medusa.gruul.addon.team.model.dto.TeamSkuDTO;
import com.medusa.gruul.addon.team.model.enums.TeamMode;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.common.model.base.StackableDiscount;
import io.vavr.control.Option;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TeamManagerServiceTest extends TestCase {

    @Autowired
    private TeamManagerService teamManagerService;


    @Test
    public void testNewActivity() {
        teamManagerService.newActivity(
                1567782336624394240L,
                new TeamDTO()
                        .setName("测试删除临时数据")
                        .setStartTime(LocalDateTime.now().plusMinutes(10))
                        .setEndTime(LocalDateTime.now().plusDays(1))
                        .setEffectTimeout(30)
                        .setMode(TeamMode.COMMON)
                        .setUsers(List.of(3))
                        .setPayTimeout(15)
                        .setSimulate(Boolean.FALSE)
                        .setHuddle(Boolean.FALSE)
//                        .setPreheat(Boolean.FALSE)
                        .setStackable(
                                new StackableDiscount()
                                        .setCoupon(Boolean.FALSE)
                                        .setVip(Boolean.FALSE)
                                        .setFull(Boolean.FALSE)
                        )
                        .setProducts(
                                List.of(
                                        new TeamProductDTO()
                                                .setProductId(1636313590951473152L)
                                                .setSkus(List.of(
                                                                new TeamSkuDTO()
                                                                        .setSkuId(1636313591145492480L)
                                                                        .setStock(2L)
                                                                        .setPrices(
                                                                                List.of(10000L)
                                                                        )
                                                        )
                                                )
                                )
                        )
        );
    }

    @Test
    public void testActivityPage() {
        TeamPageDTO teamPage = new TeamPageDTO();
        teamPage.setCurrent(1);
        teamPage.setSize(10);
        IPage<TeamPageVO> page = teamManagerService.activityPage(Option.of(1567782336624394240L), teamPage);
        System.out.println(page.getRecords());
    }

    @Test
    public void testDeleteActivityBatch() {
        teamManagerService.deleteActivityBatch(
                Set.of(
                        new TeamKey()
                                .setShopId(1567782336624394240L)
                                .setActivityId(1638412497093427200L)
                )
        );
    }

    @Test
    public void testGetActivity() {
        System.out.println(teamManagerService.getActivity(
                1567782336624394240L,
                1638412497093427200L
        ));
    }

    @Test
    public void testViolateActivity() {
    }

    @Test
    public void testOrderPage() {
    }

    @Test
    public void testGetOrderSummary() {
    }

    @Test
    public void testOrderUserPage() {
    }
}