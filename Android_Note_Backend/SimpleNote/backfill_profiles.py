from django.contrib.auth import get_user_model
from apps.accounts.models import Profile

User = get_user_model()
for u in User.objects.all():
    Profile.objects.get_or_create(user=u)

print("Backfill done.")
