#!/bin/bash

# Script to help update google-services.json with correct package name
echo "🔧 Google Services JSON Package Name Updater"
echo "============================================="

# Check if google-services.json exists
if [ ! -f "app/google-services.json" ]; then
    echo "❌ google-services.json not found in app/ directory"
    echo "Please add your google-services.json file to app/ directory first"
    exit 1
fi

echo "📄 Current google-services.json contents:"
echo "----------------------------------------"
cat app/google-services.json
echo ""

# Check current package name
current_package=$(grep -o '"package_name": "[^"]*"' app/google-services.json | cut -d'"' -f4)
echo "📱 Current package name: $current_package"

if [ "$current_package" = "com.pillreminder.app" ]; then
    echo "✅ Package name is already correct!"
    exit 0
fi

echo ""
echo "⚠️  Package name mismatch detected!"
echo "Current: $current_package"
echo "Expected: com.pillreminder.app"
echo ""

# Create backup
cp app/google-services.json app/google-services.json.backup
echo "💾 Backup created: app/google-services.json.backup"

# Update package name
sed -i 's/"package_name": "[^"]*"/"package_name": "com.pillreminder.app"/g' app/google-services.json

# Verify the change
new_package=$(grep -o '"package_name": "[^"]*"' app/google-services.json | cut -d'"' -f4)
echo "✅ Package name updated to: $new_package"

echo ""
echo "📄 Updated google-services.json contents:"
echo "----------------------------------------"
cat app/google-services.json

echo ""
echo "🎯 Next steps:"
echo "1. Copy the updated google-services.json content above"
echo "2. Update your GitHub secret GOOGLE_SERVICES_JSON with this content"
echo "3. Or use this file directly in your project"
echo ""
echo "💡 To update GitHub secret:"
echo "   Go to: Settings → Secrets and variables → Actions"
echo "   Update GOOGLE_SERVICES_JSON secret with the new content"