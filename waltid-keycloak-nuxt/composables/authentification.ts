export async function sendRequest(access_token :string){

    return await $fetch('http://localhost:8090/r/world', {
        method: 'get',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + access_token
        },
    });
}
