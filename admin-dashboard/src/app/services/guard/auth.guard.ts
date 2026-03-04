import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth-service/auth.service'; 

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated) {
    console.log("User not authenticated, redirecting to signin");
    return router.createUrlTree(['/signin']);
  }

  // Check for admin roles (since admin-only UI)
  if (!authService.isAdmin && !authService.isSuperAdmin) {
    console.log("User does not have admin privileges, redirecting to signin");
    return router.createUrlTree(['/signin']);
  }

  return true;
};
