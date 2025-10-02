const qrcode = require('qrcode-terminal');

// The Expo development server URL
const expoUrl = 'exp://127.0.0.1:8082';

console.log('📱 Your Expo App QR Code:');
console.log('URL:', expoUrl);
console.log('');
qrcode.generate(expoUrl, {small: true}, function (qrcode) {
    console.log(qrcode);
});

console.log('');
console.log('📋 Instructions:');
console.log('1. Install "Expo Go" app on your phone from App Store/Play Store');
console.log('2. Open Expo Go app');
console.log('3. Scan the QR code above with your phone camera or Expo Go app');
console.log('4. Your app will load on your phone!');
console.log('');
console.log('🌐 Alternative: You can also type this URL in Expo Go:');
console.log(expoUrl);