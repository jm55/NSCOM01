function simulateSize(){
	console.log("simulateSize()");
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
}

function combineOptsVals(){
	var optcode = [0,1];
	var finalOptsVals = [];

	//Pretend that this part is isolated.
	var opts = ['opt1', 'opt2', 'opt3'];
	var vals = ['val1', 'val2', 'val3'];
	if(opts.length == vals.length){
		for(var i = 0; i < opts.length; i++)
			finalOptsVals.append([opts[i], 0, vals[i],0]);
	}

	var combinedBytes = [optcode, finalOptsVals];
}

function combineBytes(bytes){
	var combined = [];
	for(byte of bytes){
		for(b of byte){
			combined.append(b);
		}
	}
	return combined;
}

function byteAppend(src, appendee){

}

//simulateSize();

combineOptsVals();