#!/bin/bash

# ১. নির্দিষ্ট ফাইলগুলোতে লাইসেন্স চেঞ্জ করা
find . -type f \( -name "*.txt" -o -name "*.md" -o -name "*README*" -o -name "*LICENSE*" \) -exec sed -i 's/GPL-3.0 License/MIT License/g' {} +

# ২. গিটহাবে চেঞ্জগুলো অ্যাড, কমিট আর পুশ করা
git add .
git commit -m "chore: update license from GPL-3.0 to MIT"
git push

echo "সব কাজ শেষ, এবার স্ক্রিপ্ট ডিলিট হচ্ছে..."
