from rest_framework import status
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView
from django.contrib.auth.password_validation import validate_password
from django.core.exceptions import ValidationError
from .models import Profile

from .serializers import SignupSerializer, ChangePasswordSerializer, UserMeSerializer


class SignupView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        s = SignupSerializer(data=request.data)
        s.is_valid(raise_exception=True)
        s.save()
        return Response({"detail": "account created"}, status=status.HTTP_201_CREATED)


class ChangePasswordView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        s = ChangePasswordSerializer(data=request.data)
        s.is_valid(raise_exception=True)

        user = request.user
        old = s.validated_data["old_password"]
        new = s.validated_data["new_password"]

        if not user.check_password(old):
            return Response({"old_password": ["Incorrect."]}, status=400)

        try:
            validate_password(new, user=user)
        except ValidationError as e:
            return Response({"new_password": e.messages}, status=400)

        user.set_password(new)
        user.save()
        return Response({"detail": "password changed"})


class MeView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        return Response(UserMeSerializer(request.user, context={"request": request}).data)

    def patch(self, request):
        s = UserMeSerializer(request.user, data=request.data, partial=True, context={"request": request})
        s.is_valid(raise_exception=True)
        s.save()
        return Response(s.data)



class MeAvatarView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        try:
            file = request.FILES.get("avatar")
            if not file:
                return Response({"detail": "Missing file field 'avatar'."}, status=400)

            # Ensure a Profile exists for this user (handles old users created before Profile model)
            profile, _ = Profile.objects.get_or_create(user=request.user)

            # (Optional) quick sanity checks
            if file.size > 5 * 1024 * 1024:  # 5MB
                return Response({"detail": "Image too large (max 5MB)."}, status=400)
            if not file.content_type.startswith("image/"):
                return Response({"detail": "Invalid file type."}, status=400)

            profile.avatar = file
            profile.save()

            data = UserMeSerializer(request.user, context={"request": request}).data
            return Response(data, status=200)

        except Exception as e:
            # Convert the 500 into a readable 400 so your app shows a friendly message
            return Response({"detail": f"Upload failed: {e}"}, status=400)
