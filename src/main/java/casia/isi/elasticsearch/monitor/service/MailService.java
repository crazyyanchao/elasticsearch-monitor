package casia.isi.elasticsearch.monitor.service;

import casia.isi.elasticsearch.monitor.entity.MailBean;
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
import javax.mail.internet.MimeMessage;

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
     * 发送简易邮件
     */
    @Async
    public void sendSimpleMail(MailBean mailBean) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);//发送方
        message.setTo(mailBean.getReceiver().split(";"));//接收者
        message.setSubject(mailBean.getSubject());//主题
        message.setText(mailBean.getContent());//内容
        javaMailSender.send(message);
        logger.info("简单邮件已经发送。");
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


}
