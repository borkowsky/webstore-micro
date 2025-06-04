package net.rewerk.webstore.events.controller;

import net.rewerk.webstore.dto.request.event.EventSearchDto;
import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.entity.Event;
import net.rewerk.webstore.events.configuration.SecurityConfiguration;
import net.rewerk.webstore.events.service.entity.EventService;
import net.rewerk.webstore.events.specification.EventSpecification;
import net.rewerk.webstore.utility.RequestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EventsController.class)
@Import(SecurityConfiguration.class)
@AutoConfigureMockMvc
public class EventsControllerIT {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private EventService eventService;

    @Test
    public void givenEvents_whenGetEvents_thenReturnPageWithData() throws Exception {
        List<EventResponseDto> events = List.of(EventResponseDto.builder()
                .id(1)
                .text("test event 1")
                .build(), EventResponseDto.builder()
                .id(2)
                .text("test event 2")
                .build());
        Page<EventResponseDto> page = new PageImpl<>(events);
        EventSearchDto eventSearchDto = new EventSearchDto();

        Specification<Event> spec = EventSpecification.getSpecification(eventSearchDto);
        Pageable pageable = RequestUtils.getSortAndPageRequest(eventSearchDto);

        given(eventService.findAll(spec, pageable))
                .willReturn(page);

        mvc.perform(get("/api/v1/events")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.payload", hasSize(2)),
                        jsonPath("$.page").value(1),
                        jsonPath("$.pages").value(1),
                        jsonPath("$.payload[0].text").value("test event 1")
                );
    }

    @Test
    public void givenEvents_whenGetEventsWithInvalidPage_thenReturnErrorResponse() throws Exception {
        EventSearchDto eventSearchDto = new EventSearchDto();
        eventSearchDto.setPage(1);

        Specification<Event> spec = EventSpecification.getSpecification(eventSearchDto);
        Pageable pageable = RequestUtils.getSortAndPageRequest(eventSearchDto);

        given(eventService.findAll(spec, pageable))
                .willReturn(Page.empty());

        mvc.perform(get("/api/v1/events")
                        .with(user("admin").roles("ADMIN"))
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.payload", hasSize(0)),
                        jsonPath("$.page").value(1),
                        jsonPath("$.pages").value(1)
                );

    }
}
