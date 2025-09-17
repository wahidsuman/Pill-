#!/bin/bash

# Auto-sync script for Codespace
# This script will automatically pull changes from GitHub

echo "🔄 Starting auto-sync..."

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ Not in a git repository"
    exit 1
fi

# Get current branch
CURRENT_BRANCH=$(git branch --show-current)
echo "📍 Current branch: $CURRENT_BRANCH"

# Fetch latest changes
echo "📥 Fetching latest changes..."
git fetch origin

# Check if there are new commits
LOCAL=$(git rev-parse HEAD)
REMOTE=$(git rev-parse origin/$CURRENT_BRANCH)

if [ "$LOCAL" = "$REMOTE" ]; then
    echo "✅ Already up to date"
else
    echo "🔄 New changes detected, pulling..."
    git pull origin $CURRENT_BRANCH
    echo "✅ Successfully synced with GitHub"
fi

echo "🎉 Auto-sync complete!"