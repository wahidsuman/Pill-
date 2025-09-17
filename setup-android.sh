#!/bin/bash

# Android SDK Setup Script
export ANDROID_HOME=/workspace/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator

echo "🤖 Android SDK Environment Setup Complete!"
echo "📍 ANDROID_HOME: $ANDROID_HOME"
echo "🚀 Ready to run emulator and build apps!"