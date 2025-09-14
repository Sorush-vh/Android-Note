# apps/notes/urls.py
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import NoteViewSet, LabelViewSet

router = DefaultRouter()
router.register(r"notes", NoteViewSet, basename="note")
router.register(r"labels", LabelViewSet, basename="label")

urlpatterns = [
    path("", include(router.urls)),
]
