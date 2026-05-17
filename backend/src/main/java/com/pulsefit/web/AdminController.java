package com.pulsefit.web;

import com.pulsefit.service.AdminService;
import com.pulsefit.web.dto.BillingDtos;
import com.pulsefit.web.dto.AdminDtos;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/admin", "/admin"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("/dashboard")
  AdminDtos.AdminDashboardResponse dashboard() {
    return adminService.dashboard();
  }

  @GetMapping("/users")
  AdminDtos.PaginatedUsersResponse users(@RequestParam(required = false) String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return adminService.users(query, page, size);
  }

  @PostMapping("/users/role")
  void updateRole(@RequestParam String keycloakUserId, @Valid @RequestBody AdminDtos.UpdateRoleRequest request) {
    adminService.updateUserRole(keycloakUserId, request.role());
  }

  @GetMapping("/payments")
  List<AdminDtos.PaymentRow> payments() {
    return adminService.payments();
  }

  @PostMapping("/payments/refund")
  BillingDtos.RefundResponse refund(@RequestParam String paymentId) throws Exception {
    return adminService.refund(paymentId);
  }
}
