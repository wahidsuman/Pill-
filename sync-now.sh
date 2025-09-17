#!/bin/bash

# Quick sync command - run this whenever you want to check for updates
echo "🔄 Quick sync from GitHub..."
git fetch origin
git pull origin main
echo "✅ Synced!"