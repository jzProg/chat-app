const readBlobImage = (image) => {
  const byteCharacters = atob(image);
  const byteArrays = [];
  for (let offset = 0; offset < byteCharacters.length; offset += 512) {
    const slice = byteCharacters.slice(offset, offset + 512);
    const byteNumbers = new Array(slice.length);
    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    byteArrays.push(byteArray);
  }
  const blob = new Blob(byteArrays, {type: 'image/png'});
  return window.URL.createObjectURL(blob);
};

const urlB64ToUint8Array = (base64String) => {
  const padding = '='.repeat((4 - base64String.length % 4) % 4);
  const base64 = (base64String + padding).replace(/\-/g, '+').replace(/_/g, '/');
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);
  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
};

const debounce = (cb, duration) => {
  let timer;
  let resolves = [];

  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      let result = cb(...args);
      resolves.forEach(r => r(result));
      resolves = [];
    }, duration);

    return new Promise(r => resolves.push(r));
  };
};

export default {
  readBlobImage,
  urlB64ToUint8Array,
  debounce,
};
