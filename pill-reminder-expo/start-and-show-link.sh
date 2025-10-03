#!/bin/bash

echo "🚀 Starting Expo Development Server..."
echo ""

# Start expo in background and capture output
cd /workspace/pill-reminder-expo
npx expo start --tunnel > /tmp/expo-output.txt 2>&1 &
EXPO_PID=$!

echo "⏳ Waiting for server to start..."
sleep 15

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📱 EXPO DEVELOPMENT SERVER"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ Server is running (PID: $EXPO_PID)"
echo ""
echo "📲 TO ACCESS YOUR APP:"
echo ""
echo "1️⃣  Open Expo Go app on your phone"
echo "2️⃣  Sign in with: wahidsuman"
echo "3️⃣  Look for 'pill-reminder-expo' in Projects"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🔗 Direct URLs:"
echo ""
echo "   Expo Go: exp://@wahidsuman/pill-reminder-expo"
echo "   Local:   exp://localhost:8081"
echo "   Web:     http://localhost:8081"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📱 Download Expo Go:"
echo "   iOS:     https://apps.apple.com/app/expo-go/id982107779"
echo "   Android: https://play.google.com/store/apps/details?id=host.exp.exponent"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "⌨️  Press Ctrl+C to stop the server"
echo ""

# Show the expo output
tail -f /tmp/expo-output.txt
