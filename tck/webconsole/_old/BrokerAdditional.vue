<template>
  <div>
    <b-card shadow="always" style="margin-bottom: 30px">
      <div class="emq-title">Subscribe</div>
      <b-form
        ref="subscription"
        hide-required-asterisk
        size="small"
        labb-position="top"
        :model="subscription"
      >
        <b-row>
          <b-col :span="8">
            <b-form-item prop="topic" label="Topic">
              <b-input v-model="subscription.topic"></b-input>
            </b-form-item>
          </b-col>
          <b-col :span="8">
            <b-form-item prop="qos" label="QoS">
              <b-select v-model="subscription.qos">
                <b-option
                  v-for="(item, index) in qosList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></b-option>
              </b-select>
            </b-form-item>
          </b-col>
          <b-col :span="8">
            <b-button
              :disabled="!client.connected"
              type="success"
              size="small"
              class="subscribe-btn"
              @click="doSubscribe"
            >
              {{ subscribeSuccess ? "Subscribed" : "Subscribe" }}
            </b-button>
            <b-button
              :disabled="!client.connected"
              type="success"
              size="small"
              class="subscribe-btn"
              style="margin-left: 20px"
              @click="doUnSubscribe"
              v-if="subscribeSuccess"
            >
              Unsubscribe
            </b-button>
          </b-col>
        </b-row>
      </b-form>
    </b-card>
    <b-card shadow="always" style="margin-bottom: 30px">
      <div class="emq-title">Publish</div>
      <b-form
        ref="publish"
        hide-required-asterisk
        size="small"
        labb-position="top"
        :model="publish"
      >
        <b-row>
          <b-col :span="8">
            <b-form-item prop="topic" label="Topic">
              <b-input v-model="publish.topic"></b-input>
            </b-form-item>
          </b-col>
          <b-col :span="8">
            <b-form-item prop="payload" label="Payload">
              <b-input v-model="publish.payload" size="small"></b-input>
            </b-form-item>
          </b-col>
          <b-col :span="8">
            <b-form-item prop="qos" label="QoS">
              <b-select v-model="publish.qos">
                <b-option
                  v-for="(item, index) in qosList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></b-option>
              </b-select>
            </b-form-item>
          </b-col>
        </b-row>
      </b-form>
      <b-col :span="24">
        <b-button
          :disabled="!client.connected"
          type="success"
          size="small"
          class="publish-btn"
          @click="doPublish"
        >
          Publish
        </b-button>
      </b-col>
    </b-card>
    <b-card shadow="always" style="margin-bottom: 30px">
      <div class="emq-title">Receive</div>
      <b-col :span="24">
        <b-input
          type="textarea"
          :rows="3"
          style="margin-bottom: 15px"
          v-model="receiveNews"
        ></b-input>
      </b-col>
    </b-card>
  </div>
</template>