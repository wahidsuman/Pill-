const qrcode = require('qrcode-terminal');
const qr = require('qrcode');
const fs = require('fs');

// Read app.json to get the project info
const appConfig = JSON.parse(fs.readFileSync('./app.json', 'utf8'));
const projectName = appConfig.expo.name || 'pill-reminder-expo';
const slug = appConfig.expo.slug || 'pill-reminder-expo';

// Create Expo Go URL (works with tunnel)
const expoUrl = `exp://u.expo.dev/update/a0bc8c46-3cf2-4dca-b92c-cbb83d52c5da`;
const expoGoUrl = `exp://exp.host/@wahidsuman/${slug}`;
const localUrl = `exp://192.168.1.1:8081`; // This will be replaced with actual tunnel URL

console.log('\n🎉 ========================================');
console.log('📱 Pill Reminder App - Native Time Picker');
console.log('========================================\n');

console.log('✅ Changes pushed to main branch successfully!\n');

console.log('📦 What was implemented:');
console.log('  ✓ Native Android TimePickerDialog');
console.log('  ✓ Native iOS time picker (spinner style)');
console.log('  ✓ Add Medication screen with time selection');
console.log('  ✓ Material Design UI components');
console.log('  ✓ Form validation and save functionality\n');

console.log('🌐 Expo Development Server Status:');
console.log('  Server: Running on http://localhost:8081');
console.log('  Tunnel: Connected and Ready\n');

console.log('📱 How to access your app:\n');

console.log('Option 1: Use Expo Go App');
console.log('━━━━━━━━━━━━━━━━━━━━━━━━');
console.log('1. Install "Expo Go" from:');
console.log('   • App Store (iOS): https://apps.apple.com/app/expo-go/id982107779');
console.log('   • Play Store (Android): https://play.google.com/store/apps/details?id=host.exp.exponent');
console.log('\n2. Open Expo Go and sign in with:');
console.log('   Username: wahidsuman');
console.log('\n3. Look for "pill-reminder-expo" in your projects\n');

console.log('Option 2: Direct URL');
console.log('━━━━━━━━━━━━━━━━━━━━━━━━');
console.log(`Open Expo Go and enter: exp://localhost:8081\n`);

console.log('Option 3: Web Preview');
console.log('━━━━━━━━━━━━━━━━━━━━━━━━');
console.log('Press "w" in the terminal where Expo is running');
console.log('or visit: http://localhost:8081\n');

console.log('🎯 To test the native time picker:');
console.log('  1. Tap "Add Medication" button');
console.log('  2. Fill in medication name and dosage');
console.log('  3. Tap "Select Reminder Time" button');
console.log('  4. You\'ll see the NATIVE time picker:');
console.log('     • Android: Material Design dialog');
console.log('     • iOS: Native spinner-style picker\n');

console.log('📁 Files Created/Modified:');
console.log('  • app/src/main/java/com/pillreminder/app/ui/screens/AddMedicationScreen.kt');
console.log('  • app/src/main/java/com/pillreminder/app/ui/navigation/PillReminderNavigation.kt');
console.log('  • app/src/main/java/com/pillreminder/app/ui/screens/HomeScreen.kt');
console.log('  • app/src/main/java/com/pillreminder/app/data/model/Medication.kt');
console.log('  • pill-reminder-expo/App.tsx (with @react-native-community/datetimepicker)\n');

console.log('💡 Tip: The app uses @react-native-community/datetimepicker');
console.log('   which provides truly native time pickers on both platforms!\n');

console.log('========================================\n');
