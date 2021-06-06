# crawler-example

It provides two endpoint:
 * `GET /check`
 * `POST /get-titles`

The first endpoint returns the string "OK".

The second endpoint expects a JSON likes the following:

```json
{
    "sites": [
        "https://google.ru",
        "https://github.com",
        "https://www.notfound.nf/",
        "http://localhost:8080/noPage"
    ]
}
```

and returns as answer a JSON likes:

```json
{
    "results": [
        {
            "site": "https://google.ru",
            "error": "Moved Permanently"
        },
        {
            "site": "https://github.com",
            "title": "GitHub: Where the world builds software · GitHub"
        },
        {
            "site": "https://www.notfound.nf/",
            "error": "Error connecting to https://www.notfound.nf using address www.notfound.nf:443 (unresolved: true)"
        },
        {
            "site": "http://localhost:8080/noPage",
            "error": "Not Found"
        }
    ]
}
```
