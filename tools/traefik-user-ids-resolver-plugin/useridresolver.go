package traefik_user_resolver_plugin

import (
	"context"
	"fmt"
	"net/http"
	"regexp"
)

// Config the plugin configuration.
type Config struct {

}

// CreateConfig creates and initializes the plugin configuration.
func CreateConfig() *Config {
	return &Config{}
}

// New creates and returns a plugin instance.
func New(ctx context.Context, next http.Handler, config *Config, name string) (http.Handler, error) {
// 	if len(config.FromHead) == 0 {
// 		return nil, fmt.Errorf("FromHead can't be empty")
// 	}
// 	re, err := regexp.Compile(config.Regex)
//
// 	if err != nil {
// 		return nil, fmt.Errorf("error compiling regex %q: %w", config.Regex, err)
// 	}

	return http.HandlerFunc(func(rw http.ResponseWriter, req *http.Request) {
// 		head := req.Header.Get(config.FromHead)
// 		result := re.FindString(head)
// 		if config.Prefix != "" {
// 			result = config.Prefix + result
// 		}
		req.Header.Set("TEST-HEADER", "ASD")
		next.ServeHTTP(rw, req)
	}), nil
}
