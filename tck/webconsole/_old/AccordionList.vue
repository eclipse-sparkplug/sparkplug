   <!--
      <div class="accordion" role="tablist">
        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-button block @click="changeMqttConnectFun(0)" variant="info">
              <span class="mr-2"> MQTT Configuration </span>
              <span>
                {{ mqttClient.connected ? "(connected)" : "(disconnected)" }}
              </span>
            </b-button>
          </b-card-header>
          <MqttConnect :change="tabOpen" @on-connect="mqttConnected" />
        </b-card>

        <b-card no-body class="mb-1">
          <b-card-header header-tag="header" class="p-1" role="tab">
            <b-button block @click="changeMqttConnectFun(1)" variant="info">
              Sparkplug Client Configuration
            </b-button>
          </b-card-header>
          <SparkplugClientConnect :change="tabOpen" v-model="sparkplugClient" />
        </b-card>
      </div>
    -->
