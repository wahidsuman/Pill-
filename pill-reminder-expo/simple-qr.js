const qr = require('qr-image');

const expoUrl = 'exp://127.0.0.1:8082';

console.log('📱 EXPO QR CODE');
console.log('===============');
console.log('URL: ' + expoUrl);
console.log('');

// Generate QR code as text
const qr_svg = qr.image(expoUrl, { type: 'terminal' });
qr_svg.pipe(process.stdout);

console.log('');
console.log('📋 INSTRUCTIONS:');
console.log('1. Install "Expo Go" app on your phone');
console.log('2. Open Expo Go app');
console.log('3. Scan the QR code above');
console.log('4. Your app will load!');
console.log('');
console.log('🔗 Or type this URL in Expo Go:');
console.log(expoUrl);