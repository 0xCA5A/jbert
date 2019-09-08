window.onload = function() {
    window.ui = SwaggerUIBundle({
        url: '/assets/openapi.yml',
        dom_id: '#swagger-ui',
        deepLinking: true,
        defaultModelExpandDepth: 5,
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: 'StandaloneLayout'
    });
};
