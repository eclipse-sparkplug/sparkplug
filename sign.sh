#!/bin/sh

# Debug
ls -l tck/build/hivemq-extension/sparkplug-tck-3.0.0.jar
pwd
ls -l tck/build/hivemq-extension

# Sign the TCK jar
curl -vvvvs -o tck/build/hivemq-extension/sparkplug-tck-3.0.0-signed.jar -F file=@tck/build/hivemq-extension/sparkplug-tck-3.0.0.jar https://cbi.eclipse.org/jarsigner/sign

# Debug
ls -l tck/build/hivemq-extension
