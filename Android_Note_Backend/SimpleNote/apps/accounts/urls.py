from django.urls import path
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView
from .views import SignupView, ChangePasswordView, MeView
from .views import MeAvatarView

urlpatterns = [
    path("signup/", SignupView.as_view()),
    path("change-password/", ChangePasswordView.as_view()),
    path("me/", MeView.as_view()),  # ← add this
    path("token/", TokenObtainPairView.as_view(), name="token_obtain_pair"),
    path("token/refresh/", TokenRefreshView.as_view(), name="token_refresh"),
    path("me/avatar/", MeAvatarView.as_view(), name="me-avatar")
]
