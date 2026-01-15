const fs = require('fs');
const path = 'd:\\project\\spring-cloud-demo\\gateway\\src\\main\\resources\\static\\service-demo.html';

let content = fs.readFileSync(path, 'utf8');
if (content.charCodeAt(0) === 0xFEFF) {
    content = content.slice(1);
}
fs.writeFileSync(path, content, 'utf8');
console.log('BOM removed successfully');
