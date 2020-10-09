package casia.isi.elasticsearch.monitor.service;

import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import ch.qos.logback.core.util.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Service
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private static String projectPath = StringUtils
            .substringBefore(System.getProperty("user.dir").replaceAll("\\\\", "/"), "/");

    static {
        System.setProperty("mail.mime.splitlongparameters", "false");
    }

    /**
     * @param
     * @return
     * @Description: TODO(SIMPLE EMAIL SEND)
     */
    @Async
    public void sendSimpleMail(MailBean mailBean) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); // SENDER
        String[] receivers = mailBean.getReceiver().split(";");
        message.setTo(receivers); // RECEIVER
        message.setSubject(mailBean.getSubject()); // TOPIC
        message.setText(mailBean.getContent()); // CONTENT
        javaMailSender.send(message);
        logger.info("Simple email already send! INFO[" +
                "SENDER:" + from + "," +
                "RECEIVER:" + JSONArray.parseArray(JSON.toJSONString(receivers)).toJSONString() + "]");
    }

    /**
     * 发送带多附件的邮件
     *
     * @param mailBean
     * @throws Exception
     */
    @Async
    public void sendMultiAttachmentsMail(MailBean mailBean) throws Exception {

        MimeMessage message = javaMailSender.createMimeMessage();
        // true表示需要创建一个multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(mailBean.getReceiver().split(";"));
        helper.setSubject(mailBean.getSubject());
        helper.setText(mailBean.getContent());

        String[] files = mailBean.getAttachment().split("\\|");
        for (String fileName : files) {
            String path = projectPath + "/temp/" + fileName;
            FileSystemResource file = new FileSystemResource(path);
            if (file.exists() && file.isFile()) {
                helper.addAttachment(fileName, file);
            }
        }

        javaMailSender.send(message);
        logger.info("带附件的邮件已经发送。");
    }


    /**
     * 发送邮件-邮件正文是HTML
     *
     * @param mailBean
     * @throws Exception
     */
    @Async
    public void sendMailHtml(MailBean mailBean) throws Exception {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom(from);
        helper.setTo(mailBean.getReceiver().split(";"));
        helper.setSubject(mailBean.getSubject());
        helper.setText(mailBean.getContent(), true);

        javaMailSender.send(mimeMessage);

    }

    /**
     * 内联资源（静态资源）邮件发送 由于邮件服务商不同，可能有些邮件并不支持内联资源的展示 在测试过程中，新浪邮件不支持，QQ邮件支持
     * 不支持不意味着邮件发送不成功，而且内联资源在邮箱内无法正确加载
     *
     * @param mailBean
     * @throws Exception
     */
    @Async
    public void sendMailInline(MailBean mailBean) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom(from);
        helper.setTo(mailBean.getReceiver().split(";"));
        helper.setSubject(mailBean.getSubject());

//        helper.setText(mailBean.getContent(),true);
        //        helper.addInline();
        helper.setText("my text <img src='cid:myLogo'>", true);
        helper.addInline("myLogo", new ClassPathResource("img/mylogo.gif"));
        javaMailSender.send(mimeMessage);

//        mailSender.send(new MimeMessagePreparator() {
//            public void prepare(MimeMessage mimeMessage) throws MessagingException {
//                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//                message.setFrom("me@mail.com");
//                message.setTo("you@mail.com");
//                message.setSubject("my subject");
//                message.setText("my text <img src='cid:myLogo'>", true);
//                message.addInline("myLogo", new ClassPathResource("img/mylogo.gif"));
//                message.addAttachment("myDocument.pdf", new ClassPathResource("doc/myDocument.pdf"));
//            }
//        });

    }

    /**
     * 模板邮件发送
     *
     * @param mailBean
     * @throws Exception
     */
    @Async
    public void sendMailTemplate(MailBean mailBean) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(mailBean.getReceiver().split(";"));
        helper.setSubject(mailBean.getSubject());
        helper.setText(mailBean.getContent(), true);
        javaMailSender.send(mimeMessage);

    }

    public void sendMailByProperties(MailBean mailBean) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.126.com");   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(SysConstant.DEBUG);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
//        MimeMessage message = createMimeMessage(session, SysConstant.EMAIL_SENDER, SysConstant.EMAIL_RECEIVER);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
//        transport.connect(SysConstant.EMAIL_SENDER, SysConstant.EMAIL_SENDER_PASSWORD);

        // 5. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
//        transport.sendMessage(message, message.getAllRecipients());
        transport.close();         // 6. 关闭连接
    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session     和服务器交互的会话
     * @param sendMail    发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sendMail, "yanchaoma", "UTF-8"));  // personal是昵称

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "yanchaoma", "UTF-8")); //我自己

//        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("18511085575@163.com", "yanchaoma", "UTF-8"));

        //    Cc: 抄送（可选）
//        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("yanchaoma@foxmail.com", "yanchaoma", "UTF-8"));  //我自己

        //    Bcc: 密送（可选）
//        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("yanchaoma@foxmail.com", "USER_FF", "UTF-8"));

        // 4. Subject: 邮件主题
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日HH时");
        String time = sdf.format(date);
        message.setSubject(time + "'DataStatis", "UTF-8");

        /**
         * 编写html文档
         */
        // 5. Content: 邮件正文（可以使用html标签）
        String messageoffer = FileUtil.readAllLine("config/statistics.html", "UTF-8");
        message.setContent(messageoffer, "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }

}


