function connectSubmit() {
    location.href = `/connect/${document.getElementById("username").value}/${document.getElementById("password").value}?url=${document.location.pathname}`
}