from django.contrib.auth import get_user_model
from rest_framework import serializers

User = get_user_model()


class SignupSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, min_length=8)

    class Meta:
        model = User
        fields = ["username", "email", "first_name", "last_name", "password"]

    def create(self, validated):
        user = User.objects.create_user(
            username=validated["username"],
            email=validated.get("email", ""),
            first_name=validated.get("first_name", ""),
            last_name=validated.get("last_name", ""),
            password=validated["password"],
        )
        return user


class ChangePasswordSerializer(serializers.Serializer):
    old_password = serializers.CharField(write_only=True)
    new_password = serializers.CharField(write_only=True, min_length=8)


class UserMeSerializer(serializers.ModelSerializer):
    avatar_url = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ("username", "email", "first_name", "last_name", "avatar_url")

    def get_avatar_url(self, obj):
        avatar = getattr(getattr(obj, "profile", None), "avatar", None)
        if not avatar:
            return None
        request = self.context.get("request")
        return request.build_absolute_uri(avatar.url) if request else avatar.url
