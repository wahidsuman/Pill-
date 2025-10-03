const QRCode = require('qrcode');

const expoUrl = 'exp://127.0.0.1:8082';

console.log('📱 EXPO QR CODE FOR YOUR MOBILE APP');
console.log('=====================================');
console.log('URL:', expoUrl);
console.log('');

// Generate QR code as ASCII art
QRCode.toString(expoUrl, {type: 'terminal'}, function (err, url) {
  if (err) throw err;
  console.log(url);
});

console.log('');
console.log('📋 HOW TO USE:');
console.log('1. Install "Expo Go" app on your phone');
console.log('2. Open Expo Go app');
console.log('3. Scan the QR code above');
console.log('4. Your app will load on your phone!');
console.log('');
console.log('🔗 Alternative: Type this in Expo Go:');
console.log(expoUrl);