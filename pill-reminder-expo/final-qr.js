const qr = require('qr-image');

const expoUrl = 'exp://dafw8ui-wahidsuman-8086.exp.direct:8086';

console.log('📱 YOUR EXPO APP QR CODE');
console.log('========================');
console.log('URL:', expoUrl);
console.log('');

// Generate QR code as text
const qr_svg = qr.image(expoUrl, { type: 'terminal' });
qr_svg.pipe(process.stdout);

console.log('');
console.log('📋 INSTRUCTIONS:');
console.log('1. Install "Expo Go" app on your phone');
console.log('2. Open Expo Go app');
console.log('3. Scan the QR code above OR type the URL');
console.log('4. Your app will load on your phone!');