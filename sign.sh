#!/bin/sh


# Sign the TCK jar
curl -vvvvs -o tck/build/hivemq-extension/sparkplug-tck-3.0.0-signed.jar -F 'file=@"tck/build/hivemq-extension/sparkplug-tck-3.0.0.jar"' https://cbi.eclipse.org/jarsigner/sign
