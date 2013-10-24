package hunternif.mc.atlas;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;

public class TestGit {
	public static void main(String[] args) {
		try {
			Git git = Git.open(new File("."));
			System.out.println(git.getRepository().getBranch());
			Ref master = git.getRepository().getRef("master");
			List<Ref> branches = Arrays.asList(master);
			git.getRepository().getTags();
			List<Ref> tags = git.tagList().call();
			
			// Only interested tags on branch "master":
			RevWalk rw = new RevWalk(git.getRepository());
			Ref tag = null;
			rw.setRetainBody(false);
			ListIterator<Ref> tagsIter = tags.listIterator(tags.size());
			while (tagsIter.hasPrevious()) {
				tag = tagsIter.previous();
				if (!tag.isPeeled()) {
					tag = git.getRepository().peel(tag);
				}
				RevCommit commit = rw.parseCommit(tag.getPeeledObjectId());
				List<Ref> reachableBranches = RevWalkUtils.findBranchesReachableFrom(commit, rw, branches);
				if (reachableBranches.contains(master)) {
					break;
				}
			}
			
			Iterable<RevCommit> log = git.log().call();
			RevCommit lastCommit = log.iterator().next();
			System.out.println(lastCommit.getFullMessage());
			System.out.println(ObjectId.toString(tag.getPeeledObjectId()));
			System.out.println(ObjectId.toString(lastCommit.getId()));
			System.out.println("lol");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
