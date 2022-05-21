var filesize = 12086;
var chunks = 23;
var buffer = 512;

if(filesize < buffer){
	buffer = filesize;
}
console.log("Initial file: " + filesize);
for(var i = 0; i <= chunks; i++){
	console.log(i + " = " + filesize + " => " + buffer);
	filesize -= buffer;
	if(filesize < buffer)
		buffer = filesize;
}
console.log("Remaining file: " + filesize);