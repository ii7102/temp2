package com.pulsefit.web;

import com.pulsefit.service.CatalogService;
import com.pulsefit.web.dto.PublicDtos;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/public", "/public"})
public class PublicController {

  private final CatalogService catalogService;

  public PublicController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/landing")
  PublicDtos.LandingResponse landing() {
    return catalogService.landing();
  }

  @GetMapping("/studios")
  List<PublicDtos.StudioSummary> studios(@RequestParam(required = false) String search,
      @RequestParam(required = false) String neighborhood,
      @RequestParam(required = false) String discipline) {
    return catalogService.studios(search, neighborhood, discipline);
  }

  @GetMapping("/studios/{slug}")
  PublicDtos.StudioDetailResponse studioDetail(@PathVariable String slug) {
    return catalogService.studioDetail(slug);
  }

  @GetMapping("/sessions")
  List<PublicDtos.SessionSummary> sessions(@RequestParam(required = false) String search,
      @RequestParam(required = false) String neighborhood,
      @RequestParam(required = false) String discipline,
      @RequestParam(required = false) String timeWindow,
      @RequestParam(required = false) Double maxPrice) {
    return catalogService.allUpcomingSessions(search, neighborhood, discipline, timeWindow, maxPrice);
  }
}
